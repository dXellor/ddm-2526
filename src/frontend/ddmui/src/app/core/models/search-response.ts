import { SearchResult } from "./search-result";

export interface SearchResponse {
  content: SearchResult[];
  pageable: {
    pageNumber: number,
    pageSize: number,
  },
  totalPages: number
}
