import { Component, inject, input } from '@angular/core';
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

  readonly files = input.required<RiskDocument[] | undefined>();

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
