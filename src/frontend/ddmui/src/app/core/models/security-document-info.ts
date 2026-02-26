export interface SecurityDocumentInfo{
  id: number;
  fullName: string;
  orgName: string;
  orgAddress: string;
  threatName: string;
  threatDescription: string;
  threatLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  threatSampleHash: string;
}
