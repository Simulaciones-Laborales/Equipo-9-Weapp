import { Component } from '@angular/core';
import { Form } from './components/form/form';
import { CardModule } from 'primeng/card';
import { Logo } from '@components/logo/logo';

@Component({
  selector: 'app-login',
  imports: [Form, CardModule, Logo],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export default class Login {}
