import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupCommandeComponent } from './popup-commande.component';

describe('PopupCommandeComponent', () => {
  let component: PopupCommandeComponent;
  let fixture: ComponentFixture<PopupCommandeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PopupCommandeComponent]
    });
    fixture = TestBed.createComponent(PopupCommandeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
