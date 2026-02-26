import { Component, inject, signal } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { MessagesModule } from 'primeng/messages';
import { DocumentService } from '../../core/services/document.service';
import { SecurityDocumentInfo } from '../../core/models/security-document-info';

@Component({
  selector: 'app-upload',
  imports: [
    NavbarComponent,
    CommonModule,
    FormsModule,
    ButtonModule,
    FileUploadModule,
    DialogModule,
    InputTextModule,
    DropdownModule,
    MessagesModule,
  ],
  providers: [MessageService],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.scss',
})
export class UploadComponent {
  private messageService = inject(MessageService);

  showDialog = false;
  submitting = false;
  selectedFile: File | null = null;

  parsedDocument = signal<SecurityDocumentInfo | null>(null);
  uploadData = signal<any>({
    title: '',
    description: '',
    category: '',
    fileName: '',
  });

  threatLevels = [
    { label: 'Low', value: 'LOW' },
    { label: 'Medium', value: 'MEDIUM' },
    { label: 'High', value: 'HIGH' },
    { label: 'Critical', value: 'CRITICAL' },
  ];

  documentForm = signal<SecurityDocumentInfo>({
    id: 0,
    fullName: '',
    orgName: '',
    orgAddress: '',
    threatName: '',
    threatDescription: '',
    threatLevel: 'LOW',
    threatSampleHash: '',
  });

  constructor(private documentService: DocumentService) {}

  onFileUpload(event: any) {
    const file = event.files[0];
    if (file) {
      this.selectedFile = file;
      this.documentService.parseDocument(file).subscribe({
        next: (res: SecurityDocumentInfo) => {
          this.documentForm.set(res);
          this.uploadData.update((data) => ({ ...data, fileName: file.name }));
          this.showDialog = true;
          // fileUpload.clear();
        },

        error: (err) => {
          console.error('Upload failed:', err);
          this.messageService.add({
            severity: 'error',
            summary: 'Parse Failed',
            detail: 'Document parsing failed.',
          });
        },
      });

      // this.uploadData.update((data) => ({
      //   ...data,
      //   fileName: file.name,
      // }));

      // this.messageService.add({
      //   severity: 'info',
      //   summary: 'File Selected',
      //   detail: `${file.name} (${(file.size / 1024 / 1024).toFixed(2)} MB)`,
      // });

      // Open dialog after file selection
    }
  }

  onFileRemove() {
    this.selectedFile = null;
    this.uploadData.update((data) => ({
      ...data,
      fileName: '',
    }));
    this.showDialog = false;
  }

  async onSubmit() {
    this.submitting = true;

    this.documentService.indexDocument(this.documentForm()).subscribe({
      next: (result) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Document Indexed',
          detail: `${result.threatName} indexed successfully! ID: ${result.id}`,
        });
        this.resetForm();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Save Failed',
          detail: `Failed to index document: ${error.message}`,
        });
        this.submitting = false;
      },
      complete: () => {
        this.submitting = false;
      },
    });
  }

  async onCancel() {
    this.documentService.deleteDocument(this.documentForm().id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Document Indexing canceled',
          detail: `Document has been removed from the system`,
        });
        this.resetForm();
      },
    });
  }

  private resetForm() {
    this.selectedFile = null;
    this.uploadData.set({
      title: '',
      description: '',
      category: '',
      fileName: '',
    });
    this.showDialog = false;
  }
}
