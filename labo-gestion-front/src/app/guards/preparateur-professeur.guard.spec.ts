import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { preparateurProfesseurGuard } from './preparateur-professeur.guard';

describe('preparateurProfesseurGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => preparateurProfesseurGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
