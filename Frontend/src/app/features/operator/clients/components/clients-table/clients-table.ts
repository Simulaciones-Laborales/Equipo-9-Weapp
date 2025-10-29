import { Component, input } from '@angular/core';
import { User } from '@core/models/user-model';
import { UserActivePipe } from '@pipes/user-active-pipe';
import { UserRolePipe } from '@pipes/user-role-pipe';
import { TableModule } from 'primeng/table';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-clients-table',
  imports: [TableModule, UserActivePipe, UserRolePipe, RouterLink],
  templateUrl: './clients-table.html',
  styleUrl: './clients-table.css',
})
export class ClientsTable {
  readonly clients = input.required<User[]>();
}
