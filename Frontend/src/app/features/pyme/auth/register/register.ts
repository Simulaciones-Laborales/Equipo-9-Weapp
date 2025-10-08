import { Component } from '@angular/core';
import { UserForm } from './user-form/user-form';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-register',
  imports: [UserForm, Card],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export default class Register {}
