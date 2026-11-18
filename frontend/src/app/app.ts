import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Avisos } from './components/avisos/avisos';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Avisos],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
