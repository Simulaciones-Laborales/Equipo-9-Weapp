import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Card } from "primeng/card";

@Component({
  selector: 'hero-section',
  imports: [Card],
  templateUrl: './hero-section.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeroSectionComponent { }
