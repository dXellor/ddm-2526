import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {

  constructor(private router: Router){}

  logout(){
    localStorage.clear()
    this.router.navigate(['/login'])
  }

  navigate(route: string){
    this.router.navigate([route])
  }
}
