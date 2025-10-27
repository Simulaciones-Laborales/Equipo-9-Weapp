import { Component, inject } from '@angular/core';
import { Subtitle } from '@components/subtitle/subtitle';
import { RiskDocument } from '@core/models/risk-document-model';
import { FileDownloaderService } from '@core/services/file-downloader-service';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-files-section',
  imports: [Subtitle, Button],
  templateUrl: './files-section.html',
  styleUrl: './files-section.css',
})
export class FilesSection {
  private readonly _fileDownloaderService = inject(FileDownloaderService);

  readonly files: RiskDocument[] = [
    {
      id: 'sdfw',
      name: 'archivo_1.doc',
      scoreImpact: 24,
      documentUrl:
        'https://drive.google.com/file/d/1DydtlOzI-j5_S0FlYU3SsDI1DP8lH_cb/view?usp=sharing',
    },
    {
      id: 'jlsfjks',
      name: 'archivo_2_con_nombre_muy_largo extremadamente_largo.doc',
      scoreImpact: 17,
      documentUrl:
        'https://drive.google.com/file/d/1DydtlOzI-j5_S0FlYU3SsDI1DP8lH_cb/view?usp=sharing',
    },
  ];

  downloadFile(url: string, name: string) {
    this._fileDownloaderService.download(url).subscribe((blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = name;
      a.click();
      URL.revokeObjectURL(objectUrl);
    });
  }
}
