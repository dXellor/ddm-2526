package com.ddm.server.api.controllers.search;

import com.ddm.server.bll.contracts.ISearchService;
import com.ddm.server.bll.dtos.search.ParameterSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("search")
public class SearchController {

    private final ISearchService searchService;

    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("parameter")
    public ResponseEntity<?> searchByParameter(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ParameterSearchRequest request, Pageable pageable){
        try {
            return ResponseEntity.ok(this.searchService.parameterSearch(request, pageable));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
