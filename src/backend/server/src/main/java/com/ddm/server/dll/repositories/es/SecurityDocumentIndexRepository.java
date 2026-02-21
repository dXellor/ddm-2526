package com.ddm.server.dll.repositories.es;

import com.ddm.server.dll.models.SecurityDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface SecurityDocumentIndexRepository extends ElasticsearchRepository<SecurityDocument, Long> {
}
