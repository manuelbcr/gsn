import { HttpClient } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FavoritesService } from '../services/favorites.service';
import { DownloadService } from '../services/download.service';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { AppComponent } from '../app.component';
import { LoginService } from '../services/login.service';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-sensor-detail',
  templateUrl: './sensor-detail.component.html',
  styleUrls: ['./sensor-detail.component.scss']
})
export class SensorDetailComponent {
  loading: boolean = true;
  sensorName: string = '';
  today = new Date().toJSON();
  yesterday = new Date((new Date()).getTime() - (1000 * 60 * 60)).toJSON();

  date = {
    from: {
      date: this.yesterday.slice(0, 19),
      config: {
        dropdownSelector: '#dropdown2',
        minuteStep: 1,
      },
      onTimeSet: () => {
        if (new Date(this.date.from.date) > new Date(this.date.to.date)) {
          this.date.to.date = this.date.from.date;
        }
      },
    },
    to: {
      date: this.today.slice(0, 19),
      config: {
        dropdownSelector: '#dropdown2',
        minuteStep: 1,
      },
      onTimeSet: () => {
        if (new Date(this.date.from.date) > new Date(this.date.to.date)) {
          this.date.from.date = this.date.to.date;
        }
      },
    },
  };

  chartConfig = {
    options: {
      chart: {
        zoomType: 'x'
      },
      rangeSelector: {
        enabled: true
      },
      navigator: {
        enabled: true
      },
      legend: {
        enabled: true
      },
      plotOptions: {
        series: {
          marker: {
            enabled: false
          }
        }
      }
    },
    series: [] as any[],
    title: {
      text: 'Data'
    },
    useHighStocks: true,
    size: {
      height: 500
    },
    yAxis: {
      labels: {
        align: 'left'
      }
    }
  }
  formGroup = this.formBuilder.group({
    fromDate: [''], // Initial value for fromDate
    toDate: [''], // Initial value for toDate
  });
  pageSize: FormControl = new FormControl(25);
  dateFormGroup = this.formBuilder.group({
    startDate: new FormControl(new Date(new Date().getTime() - 1000 * 60 * 60)),
    endDate: new FormControl(new Date())
  }
  )

  truePageSize: number = 25;
  columns: boolean[] = [true, false, true];
  filterFunctionList: (() => boolean)[] = [];
  filterValuesList: any[] = [];
  filterOperators: string[] = ['==', '!=', '>=', '>', '<=', '<'];
  details: any;
  series: any;

  // Pagination properties
  pagedValues: any[] = [];
  currentPage: number = 1;
  constructor(
    @Inject(DOCUMENT) private document: Document,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    private downloadService: DownloadService,
    private favoritesService: FavoritesService,
    private loginService: LoginService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.sensorName = this.route.snapshot.params['sensorname'];
    this.load();

  }

  updateRowCount() {
    this.truePageSize = Number(this.pageSize.value)
    this.updatePagedValues();
  }

  load() {
    const today = new Date().toJSON();
    const yesterday = new Date(new Date().getTime() - 1000 * 60 * 60).toJSON();

    this.date.from.date = yesterday.slice(0, 19);
    this.date.to.date = today.slice(0, 19);

    this.http.get(`http://localhost:8000/sensors/${this.sensorName}/${this.date.from.date}/${this.date.to.date}/`, { withCredentials: true }).subscribe(
      (data: any) => {
        this.loading = false;
        this.details = data.properties ? data : undefined;
        console.log(this.details)
        this.buildData(this.details);
        this.updatePagedValues();
      },
      error => {
        console.log(error)
        // Handle error
      }
    );
  }

  submit() {
    const startDateControl = this.dateFormGroup.get('startDate');
    const startDate = startDateControl ? startDateControl.value : null;
    const endDateControl = this.dateFormGroup.get('endDate');
    const endDate = endDateControl ? endDateControl.value : null;

    if (startDate != null && endDate != null) {
      const from = new Date(startDate).toJSON();
      const to = new Date(endDate).toJSON();
      this.date.from.date = from.slice(0, 19);
      this.date.to.date = to.slice(0, 19);
      this.http.get(`http://localhost:8000/sensors/${this.sensorName}/${this.date.from.date}/${this.date.to.date}/`, { withCredentials: true }).subscribe(
        (data: any) => {
          this.loading = false;
          this.details = data.properties ? data : undefined;
          console.log(this.details)
          this.buildData(this.details);
          this.updatePagedValues();
        },
        error => {
          console.log(error)
          // Handle error
        }
      );
    } else {
      this.load()
    }

  }
  onFromDateChange() {
    if (new Date(this.date.from.date) > new Date(this.date.to.date)) {
      this.date.to.date = this.date.from.date;
    }
  }

  onToDateChange() {
    if (new Date(this.date.from.date) > new Date(this.date.to.date)) {
      this.date.to.date = this.date.from.date;
    }
  }

  buildData(details: any) {
    if (details != undefined && details.properties.values) {
      let offset = 0;

      /**
       * 
       *       this.chartConfig.series = [] as { name: string; id: number; data: Array<Array<any>> }[];
  
      for (let k = 2; k < details.properties.fields.length; k++) {
        const series = {
          name: `${details.properties.fields[k].name} (${details.properties.fields[k].unit !== null ? details.properties.fields[k].unit : 'no unit'})`,
          id: k,
          data: [] as Array<Array<any>>
        };
  
        for (let i = 0; i < details.properties.values.length; i++) {
          if (typeof details.properties.values[i][k] === 'string' || details.properties.values[i][k] instanceof String) {
            offset++;
            break;
          }
          const array = [details.properties.values[i][1], details.properties.values[i][k]];
          series.data.push(array);
        }
  
        series.data.sort((a: any, b: any) => a[0] - b[0]);
        this.chartConfig.series.push(series);
      }
  
      this.chartConfig.series = this.chartConfig.series.filter((serie: any) => serie.data.length > 0);
       * 
       * 
       */

    }
  }



  download() {
    //this.downloadService.download(this);
  }

  addFavorite(sensorName: string) {
    this.favoritesService.add(sensorName).subscribe((resp) => {
      console.log(resp);
      this.load();
    }, (error: any) => {
      if (error.status == 302) {
        console.log(error)
        this.login();
      } else {
        console.error(error);
      }
    });
  }

  login(): void {
    this.loginService.getLoginUrl().subscribe((data: any) => {
      this.document.location.href = data.url;
    }, (error: any) => {
      console.error(error);
    });
  }

  removeFavorite(sensorName: string) {
    this.favoritesService.remove(sensorName).subscribe(() => {
      this.load();
    });
  }

  downloadCsv() {
    const sensorList = []
    sensorList.push(this.sensorName);
    const start = this.dateFormGroup.get('startDate');
    const end = this.dateFormGroup.get('endDate');
    let from;
    let to;
    if (start && start.value && start.value instanceof Date) {
      from = start && start.value ? start.value.toJSON() : '0';
    } else {
      from = start && start.value ? new Date(start.value).toJSON() : '0';
    }

    if (end && end.value && end.value instanceof Date) {
      to = end && end.value ? end.value.toJSON() : '0';
    } else {
      to = end && end.value ? new Date(end.value).toJSON() : '0';
    }


    if (from != '0' && to != '0') {
      this.downloadService.downloadMultiple(sensorList, from.slice(0, 19), to.slice(0, 19));
    } else {
      console.log("no dates")
    }

  }



  pageChanged(page: number) {
    this.currentPage = page;
    this.updatePagedValues();
  }

  updatePagedValues() {
    const startIndex = (this.currentPage - 1) * this.truePageSize;
    const endIndex = startIndex + this.truePageSize;
    this.pagedValues = this.details.properties.values.slice(startIndex, endIndex);
  }

  totalPages(): number {
    return Math.ceil(this.details.properties.values.length / this.truePageSize);
  }
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagedValues();
    }
  }

  nextPage() {
    const totalPages = this.totalPages();
    if (this.currentPage < totalPages) {
      this.currentPage++;
      this.updatePagedValues();
    }
  }
}
