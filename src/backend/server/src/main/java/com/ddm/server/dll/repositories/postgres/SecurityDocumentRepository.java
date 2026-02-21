package com.ddm.server.dll.repositories.postgres;

import com.ddm.server.dll.models.SecurityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SecurityDocumentRepository extends JpaRepository<SecurityDocument, Long> {
}
