import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FavoritesService } from '../services/favorites.service';
import { DownloadService } from '../services/download.service';
import { FormBuilder, FormControl } from '@angular/forms';

@Component({
  selector: 'app-sensor-detail',
  templateUrl: './sensor-detail.component.html',
  styleUrls: ['./sensor-detail.component.scss']
})
export class SensorDetailComponent {
  loading: boolean = true;
  sensorName: string = '';
  truePageSize: number = 25;
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
pageSize = new FormControl('');
  columns: boolean[] = [true, false, true];
  filterFunctionList: (() => boolean)[] = [];
  filterValuesList: any[] = [];
  filterOperators: string[] = ['==', '!=', '>=', '>', '<=', '<'];
  details: any;
  series: any;
  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private downloadService: DownloadService,
    private favoritesService: FavoritesService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.sensorName = this.route.snapshot.params['sensorname'];
    this.load();
  }

  updateRowCount(pageSize:any) {
    this.truePageSize = Number(pageSize);
  }

  load() {
    const today = new Date().toJSON();
    const yesterday = new Date(new Date().getTime() - 1000 * 60 * 60).toJSON();

    this.date.from.date = yesterday.slice(0, 19);
    this.date.to.date = today.slice(0, 19);

    this.http.get(`http://localhost:8000/sensors/${this.sensorName}/${this.date.from.date}/${this.date.to.date}/`).subscribe(
      (data: any) => {
        this.loading = false;
        this.details = data.properties ? data : undefined;
        console.log(this.details)
        this.buildData(this.details);
      },
      error => {
        console.log(error)
        // Handle error
      }
    );
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
    if (details !=undefined && details.properties.values) {
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
    this.favoritesService.add(sensorName).subscribe(() => {
      this.load();
    });
  }

  removeFavorite(sensorName: string) {
    this.favoritesService.remove(sensorName).subscribe(() => {
      this.load();
    });
  }


  downloadCsv() {
    //window.open(`download/${this.sensorName}/${this.date.from.date}/${this.date.to.date}`);
  }
}
