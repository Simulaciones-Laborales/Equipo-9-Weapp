import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompaniesListSection } from './companies-list-section';

describe('CompaniesListSection', () => {
  let component: CompaniesListSection;
  let fixture: ComponentFixture<CompaniesListSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompaniesListSection]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompaniesListSection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
