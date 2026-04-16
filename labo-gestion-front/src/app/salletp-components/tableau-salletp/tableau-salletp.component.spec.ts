import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableauSalletpComponent } from './tableau-salletp.component';

describe('TableauSalletpComponent', () => {
  let component: TableauSalletpComponent;
  let fixture: ComponentFixture<TableauSalletpComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableauSalletpComponent]
    });
    fixture = TestBed.createComponent(TableauSalletpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
