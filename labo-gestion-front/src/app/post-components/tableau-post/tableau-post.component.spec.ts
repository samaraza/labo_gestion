import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableauPostComponent } from './tableau-post.component';

describe('TableauPostComponent', () => {
  let component: TableauPostComponent;
  let fixture: ComponentFixture<TableauPostComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableauPostComponent]
    });
    fixture = TestBed.createComponent(TableauPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
