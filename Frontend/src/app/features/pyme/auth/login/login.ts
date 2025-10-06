import { Component } from '@angular/core';
import { Form } from './components/form/form';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-login',
  imports: [Form, CardModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export default class Login {}
