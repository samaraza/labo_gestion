import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { administrateurGuard } from './administrateur.guard';

describe('administrateurGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => administrateurGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
