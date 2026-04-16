import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableauInventaireComponent } from './tableau-inventaire.component';

describe('TableauInventaireComponent', () => {
  let component: TableauInventaireComponent;
  let fixture: ComponentFixture<TableauInventaireComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableauInventaireComponent]
    });
    fixture = TestBed.createComponent(TableauInventaireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
