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
import { DocumentService } from '../../core/services/document.service';
import {MenubarModule} from 'primeng/menubar'
import { Router } from '@angular/router';
import { NavbarComponent } from "../navbar/navbar.component";

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
    MenubarModule,
    NavbarComponent
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

  constructor(private searchService: SearchService, private documentService: DocumentService, private router: Router) {}

  //Pagination
  totalRecords = 0;
  rows = 10;
  currentPage = 0;
  selectedDocument: any

  searchQuery = '';
  knnQuery = '';
  booleanQuery = '';
  geoAddress = '';
  geoRadius!: number;

  results: SearchResult[] = [];

  columns = [
    { field: 'id', header: 'ID' },
    { field: 'fullName', header: 'Nadlezni forenzicar' },
    { field: 'orgName', header: 'Naziv CERT organizacije' },
    { field: 'orgAddress', header: 'Adresa organizacije' },
    { field: 'threatName', header: 'Naziv pretnje' },
    { field: 'threatLevel', header: 'Nivo pretnje' },
    { field: 'threatSampleHash', header: 'Hes uzorka' },
    { field: 'contentSummary', header: 'Sazetak' },
  ];

  onPageChange(event: any) {
    const page = event.page;
    this.rows = event.rows;
    this.search(page);
  }

  search(page: number = 0) {
    switch (this.activeTab) {
      case 0:
        this.searchService
          .parameterSearch(
            {
              fieldName: this.selectedField,
              value: this.searchQuery,
            },
            page,
          )
          .subscribe((res) => {
            this.results = res.content;
            this.currentPage = res.pageable.pageNumber;
          });
        break;

      case 1:
        this.searchService
          .booleanSearch(
            {
              query: this.booleanQuery
            },
            page,
          )
          .subscribe((res) => {
            this.results = res.content;
            this.currentPage = res.pageable.pageNumber;
          });
        break;

      case 2:
        this.searchService
          .geoSearch(
            {
              address: this.geoAddress,
              radius: this.geoRadius,
            },
            page,
          )
          .subscribe((res) => {
            this.results = res.content;
            this.currentPage = res.pageable.pageNumber;
          });
        break;

      case 3:
        this.searchService
          .knnSearch(
            {
              query: this.knnQuery,
            },
            page,
          )
          .subscribe((res) => {
            this.results = res.content;
            this.currentPage = res.pageable.pageNumber;
          });
        break;

      default:
        this.results = [];
    }
  }

  downloadRowFile(event: any) {
  this.documentService.downloadFile(event.id).subscribe(blob => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = event.filename || 'file';
    a.click();
    window.URL.revokeObjectURL(url);
  }, err => {
    console.error('File download error', err);
  });
}
}
