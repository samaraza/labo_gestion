import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableauPreparationComponent } from './tableau-preparation.component';

describe('TableauPreparationComponent', () => {
  let component: TableauPreparationComponent;
  let fixture: ComponentFixture<TableauPreparationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableauPreparationComponent]
    });
    fixture = TestBed.createComponent(TableauPreparationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
