import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupFournisseurComponent } from './popup-fournisseur.component';

describe('PopupFournisseurComponent', () => {
  let component: PopupFournisseurComponent;
  let fixture: ComponentFixture<PopupFournisseurComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PopupFournisseurComponent]
    });
    fixture = TestBed.createComponent(PopupFournisseurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
