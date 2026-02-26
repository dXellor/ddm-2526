import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { CommonModule } from '@angular/common';
import { TabsModule } from 'primeng/tabs';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { SearchService } from '../../core/services/search.service';
import { SearchResponse } from '../../core/models/search-response';
import { SearchResult } from '../../core/models/search-result';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    FormsModule,
    TabsModule,
    TableModule,
    InputTextModule,
    ButtonModule,
    CardModule,
    DropdownModule,
  ],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  activeTab = 0;
  selectedField: string = '';
  searchFields = [
    { label: 'Nadlezni forenzicar', value: 'fullName' },
    { label: 'Hes vrednost', value: 'threatSampleHash' },
    { label: 'Nivo pretnje', value: 'threatLevel' },
    { label: 'Naziv CIRT organizacije', value: 'orgName' },
    { label: 'Naziv pretnje', value: 'threatName' },
  ];

  constructor(private searchService: SearchService) {}

  //Pagination
  totalRecords = 0;
  rows = 10;
  currentPage = 0;

  searchQuery = '';
  booleanQuery = '';
  latitude!: number;
  longitude!: number;
  knnValue!: number;

  results: SearchResult[] = [];

  columns = [
    { field: 'id', header: 'ID' },
    { field: 'fullName', header: 'Nadlezni forenzicar' },
    { field: 'orgName', header: 'Naziv CIRT organizacije' },
    { field: 'orgAddress', header: 'Adresa organizacije' },
    { field: 'threatName', header: 'Naziv pretnje' },
    { field: 'threatDescription', header: 'Opis pretnje' },
    { field: 'threatLevel', header: 'Nivo pretnje' },
    { field: 'threatSampleHash', header: 'Hes uzorka' },
  ];

  onPageChange(event: any) {
    const page = event.page;
    this.rows = event.rows;
    this.search(page);
  }

  search(page: number = 0) {
    if (this.activeTab === 0) {
      console.log('Simple Search');
      console.log('Field:', this.selectedField);
      console.log('Query:', this.searchQuery);

      this.searchService
        .parameterSearch({
          fieldName: this.selectedField,
          value: this.searchQuery,
        })
        .subscribe((res) => {
          this.results = res.content;
          this.currentPage = res.pageable.pageNumber;
        });
    }
  }
}
