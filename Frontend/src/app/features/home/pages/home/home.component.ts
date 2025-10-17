import { ChangeDetectionStrategy, Component } from '@angular/core';
import { HeroSectionComponent } from "@features/home/components/hero-section/hero-section.component";
import { FeaturesSectionComponent } from "@features/home/components/features-section/features-section.component";
import { CtaSectionComponent } from "@features/home/components/cta-section/cta-section.component";
import { HeaderComponent } from "@components/header/header.component";

@Component({
  selector: 'app-home',
  imports: [HeroSectionComponent, FeaturesSectionComponent, CtaSectionComponent, HeaderComponent],
  templateUrl: './home.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class HomeComponent { }
