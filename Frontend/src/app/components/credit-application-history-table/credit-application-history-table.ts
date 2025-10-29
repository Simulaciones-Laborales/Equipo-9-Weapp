import { DatePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { HistoryStore } from '@core/stores/history-store';
import { HistoryActionTypePipe } from '@pipes/history-action-type-pipe';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-credit-application-history-table',
  imports: [TableModule, DatePipe, HistoryActionTypePipe, LoadingSpinner],
  templateUrl: './credit-application-history-table.html',
  styleUrl: './credit-application-history-table.css',
  providers: [HistoryStore],
})
export class CreditApplicationHistoryTable {
  readonly creditId = input.required<string>();
  readonly store = inject(HistoryStore);

  async ngOnInit() {
    this.store.setCreditId(this.creditId());
    await this.store.fetch();
  }
}
