import { Component, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-navbar-link',
  imports: [RouterLink, RouterLinkActive, NgClass],
  templateUrl: './navbar-link.html',
  styleUrl: './navbar-link.css',
})
export class NavbarLink {
  readonly headline = input.required<string>();
  readonly route = input.required<string>();
}
