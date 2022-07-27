import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAccountFormComponent } from './user-account-form.component';

describe('UserAccountFormComponent', () => {
  let component: UserAccountFormComponent;
  let fixture: ComponentFixture<UserAccountFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserAccountFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAccountFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
