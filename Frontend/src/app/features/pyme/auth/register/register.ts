import { Component } from '@angular/core';
import { Form } from './form/form';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-register',
  imports: [Form, Card],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export default class Register {}
