import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Card } from "primeng/card";

@Component({
  selector: 'features-section',
  imports: [Card],
  templateUrl: './features-section.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeaturesSectionComponent { }
