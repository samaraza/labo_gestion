import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { administrateurDirecteurGuard } from './administrateur-directeur.guard';

describe('administrateurDirecteurGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => administrateurDirecteurGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
