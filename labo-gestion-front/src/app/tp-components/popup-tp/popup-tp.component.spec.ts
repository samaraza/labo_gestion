import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupTpComponent } from './popup-tp.component';

describe('PopupTpComponent', () => {
  let component: PopupTpComponent;
  let fixture: ComponentFixture<PopupTpComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PopupTpComponent]
    });
    fixture = TestBed.createComponent(PopupTpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
