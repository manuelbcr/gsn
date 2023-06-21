import { TestBed } from '@angular/core/testing';

import { SensorListService } from './sensor-list.service';

describe('SensorListService', () => {
  let service: SensorListService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SensorListService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
