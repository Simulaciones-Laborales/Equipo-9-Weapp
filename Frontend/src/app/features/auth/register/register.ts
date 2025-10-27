import { Component } from '@angular/core';
import { Form } from './form/form';
import { Card } from 'primeng/card';
import { Logo } from '@components/logo/logo';

@Component({
  selector: 'app-register',
  imports: [Form, Card, Logo],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export default class Register {}
