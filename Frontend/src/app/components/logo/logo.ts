import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-logo',
  imports: [RouterLink, NgClass],
  templateUrl: './logo.html',
  styleUrl: './logo.css',
})
export class Logo {
  readonly size = input<'md' | 'lg' | 'xl'>('md');
  readonly align = input<'left' | 'center'>('left');
}
