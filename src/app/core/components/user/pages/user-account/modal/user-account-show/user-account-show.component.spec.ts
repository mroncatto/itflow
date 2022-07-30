import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAccountShowComponent } from './user-account-show.component';

describe('UserAccountShowComponent', () => {
  let component: UserAccountShowComponent;
  let fixture: ComponentFixture<UserAccountShowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserAccountShowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAccountShowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
