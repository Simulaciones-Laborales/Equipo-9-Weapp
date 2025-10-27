import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Button } from 'primeng/button';
import { Divider } from 'primeng/divider';

@Component({
  selector: 'app-linker',
  imports: [Button, RouterLink, Divider],
  templateUrl: './linker.html',
  styleUrl: './linker.css',
})
export class Linker {
  readonly route = input.required<string>();
  readonly text = input.required<string>();
}
