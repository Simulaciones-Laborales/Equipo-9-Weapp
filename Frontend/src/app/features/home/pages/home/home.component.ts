import { ChangeDetectionStrategy, Component } from '@angular/core';
import { HeaderComponent } from "@features/home/components/header/header.component";
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [HeaderComponent, RouterOutlet],
  templateUrl: './home.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class HomeComponent { }
