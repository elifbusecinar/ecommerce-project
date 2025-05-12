import { Component } from '@angular/core';
import { RouterModule } from '@angular/router'; // RouterModule import edildi
import { CommonModule } from '@angular/common'; // NgFor, NgIf gibi direktifler için eklenebilir

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterModule, // routerLink direktifinin çalışması için eklendi
    CommonModule  // *ngIf, *ngFor gibi Angular direktifleri için gerekebilir
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {

  constructor() { }

  // Dashboard'a özel bir mantık gerekirse buraya eklenebilir
  // Örneğin, özet istatistikleri backend'den çekmek gibi.

}
