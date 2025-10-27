import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-subtitle',
  imports: [NgClass],
  templateUrl: './subtitle.html',
  styleUrl: './subtitle.css',
})
export class Subtitle {
  readonly headline = input.required<string>();
  readonly align = input<'left' | 'center' | 'right'>('left');
}
