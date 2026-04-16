import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupAffecterUserComponent } from './popup-affecter-user.component';

describe('PopupAffecterUserComponent', () => {
  let component: PopupAffecterUserComponent;
  let fixture: ComponentFixture<PopupAffecterUserComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PopupAffecterUserComponent]
    });
    fixture = TestBed.createComponent(PopupAffecterUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
