import { Component } from '@angular/core';
import { RouterLink } from '@angular/router'; // Import for RouterLink

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [RouterLink], // Add RouterLink to imports
  templateUrl: './forbidden.component.html',
  styleUrls: ['./forbidden.component.scss']
})
export class ForbiddenComponent {

  constructor() { }

}
