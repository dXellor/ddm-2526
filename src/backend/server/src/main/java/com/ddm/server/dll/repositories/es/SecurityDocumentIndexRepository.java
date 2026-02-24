package com.ddm.server.dll.repositories.es;

import com.ddm.server.dll.models.SecurityDocumentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SecurityDocumentIndexRepository extends ElasticsearchRepository<SecurityDocumentIndex, Long> {
}
