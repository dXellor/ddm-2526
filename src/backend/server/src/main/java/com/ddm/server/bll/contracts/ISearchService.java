package com.ddm.server.bll.contracts;

import com.ddm.server.bll.dtos.search.GeoPointSearchRequest;
import com.ddm.server.bll.dtos.search.KnnSearchRequest;
import com.ddm.server.bll.dtos.search.ParameterSearchRequest;
import com.ddm.server.bll.dtos.search.SecurityDocumentSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISearchService {

    Page<SecurityDocumentSearchResponse> parameterSearch(ParameterSearchRequest request, Pageable pageable) throws Exception;
    Page<SecurityDocumentSearchResponse> knnSearch(KnnSearchRequest request, Pageable pageable) throws Exception;
    Page<SecurityDocumentSearchResponse> semiStructuredSearch(String query, Pageable pageable) throws Exception;
    Page<SecurityDocumentSearchResponse> geoPointSearch(GeoPointSearchRequest request, Pageable pageable) throws Exception;
}
