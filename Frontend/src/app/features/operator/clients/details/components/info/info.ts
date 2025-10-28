import { Component, input } from '@angular/core';
import { User } from '@core/models/user-model';
import { InfoItem } from '../info-item/info-item';
import { UserActivePipe } from '@pipes/user-active-pipe';

@Component({
  selector: 'app-info',
  imports: [InfoItem, UserActivePipe],
  templateUrl: './info.html',
  styleUrl: './info.css',
})
export class Info {
  readonly user = input.required<User | null>();
}
