package com.ddm.server.bll.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ddm.server.bll.contracts.ISearchService;
import com.ddm.server.bll.dtos.search.GeoPointSearchRequest;
import com.ddm.server.bll.dtos.search.KnnSearchRequest;
import com.ddm.server.bll.dtos.search.ParameterSearchRequest;
import com.ddm.server.bll.dtos.search.SecurityDocumentSearchResponse;
import com.ddm.server.dll.models.SecurityDocument;
import com.ddm.server.dll.models.SecurityDocumentIndex;
import com.ddm.server.dll.repositories.es.SecurityDocumentIndexRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService implements ISearchService {

    private final SecurityDocumentIndexRepository indexRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final SentanceTransformService sentanceTransformService;
    private final GeoCodingService geoCodingService;

    public SearchService(SecurityDocumentIndexRepository indexRepository, ElasticsearchClient elasticsearchClient, SentanceTransformService sentanceTransformService, GeoCodingService geoCodingService) {
        this.indexRepository = indexRepository;
        this.elasticsearchClient = elasticsearchClient;
        this.sentanceTransformService = sentanceTransformService;
        this.geoCodingService = geoCodingService;
    }


    @Override
    public Page<SecurityDocumentSearchResponse> parameterSearch(ParameterSearchRequest request, Pageable pageable) throws Exception {
        try {
            SearchResponse<SecurityDocumentIndex> response = this.elasticsearchClient.search(s -> s
                            .index("security_documents")
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize())
                            .query(q -> q
                                    .match(m -> m
                                            .field(request.getFieldName())
                                            .fuzziness("AUTO")
                                            .query(request.getValue())
                                    )
                            ),
                    SecurityDocumentIndex.class
            );

            return new PageImpl<>(response.hits().hits().stream().map(hit -> new SecurityDocumentSearchResponse(hit.source())).collect(Collectors.toList()), pageable, response.hits().hits().size());
        } catch (Exception e) {
            log.error("Error when making an simple index search {}", e.getMessage());
            throw new Exception("Error searching the indexed documents.");
        }
    }

    @Override
    public Page<SecurityDocumentSearchResponse> knnSearch(KnnSearchRequest request, Pageable pageable) throws Exception {
        try{
            float[] queryVector = this.sentanceTransformService.embedText(request.getQuery());

            List<Float> floatList = new ArrayList<>(queryVector.length);
            for (float f : queryVector) {
                floatList.add(f);
            }

            SearchResponse<SecurityDocumentIndex> response = this.elasticsearchClient.search(s -> s
                            .index("security_documents")
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize())
                            .minScore(0.7)
                            .query(q -> q
                                    .bool(b -> b
                                            .should(sb -> sb
                                                    .knn(knn -> knn
                                                            .field("vectorizedContent")
                                                            .queryVector(floatList)
                                                            .numCandidates(100)
                                                    )
                                            )
                                    )
                            ),
                    SecurityDocumentIndex.class
            );
            return new PageImpl<>(response.hits().hits().stream().map(hit -> new SecurityDocumentSearchResponse(hit.source())).collect(Collectors.toList()), pageable, response.hits().hits().size());
//            return this.returnSearchResults(response,SearchType.KNN, Collections.singletonList(fieldName), page);
        } catch (Exception e) {
            log.error("Error when making an knn index search {}", e.getMessage());
            throw new Exception("Error searching the indexed documents.");
        }
    }

    @Override
    public Page<SecurityDocumentSearchResponse> semiStructuredSearch(String query, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SecurityDocumentSearchResponse> geoPointSearch(GeoPointSearchRequest request, Pageable pageable) throws Exception {
        try{
            GeoPoint location = this.geoCodingService.geocodeAddress(request.getAddress());
            LatLonGeoLocation latLon = new LatLonGeoLocation.Builder()
                    .lat(location.getLat())
                    .lon(location.getLon())
                    .build();
            SearchResponse<SecurityDocumentIndex> response = elasticsearchClient.search(s -> s
                            .index("security_documents")
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize())
                            .query(q -> q
                                    .geoDistance(g -> g
                                            .field("geoPoint")
                                            .distance(request.getDistance().toString() + "km")
                                            .location(l -> l.latlon(latLon))
                                    )
                            ),
                    SecurityDocumentIndex.class
            );

            return new PageImpl<>(response.hits().hits().stream().map(hit -> new SecurityDocumentSearchResponse(hit.source())).collect(Collectors.toList()), pageable, response.hits().hits().size());
//            return this.returnSearchResults(response, SearchType.GEOLOCATION, Collections.singletonList("attackedOrganizationAddress"), page);
        } catch (Exception e) {
            log.error("Error when making an geocoding index search {}", e.getMessage());
            throw new Exception("Error geolocation searching the indexed documents.");
        }
    }
}
