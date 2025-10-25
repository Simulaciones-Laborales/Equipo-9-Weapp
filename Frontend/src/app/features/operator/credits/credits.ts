import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Button } from 'primeng/button';
import { Header } from '../components/header/header';
import { Tab, TabList, TabPanel, Tabs, TabPanels } from 'primeng/tabs';

@Component({
  selector: 'app-credits',
  imports: [
    TableModule,
    CurrencyPipe,
    DatePipe,
    Button,
    Header,
    Tabs,
    TabList,
    Tab,
    TabPanel,
    TabPanels,
  ],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
})
export default class Credits {
  today = new Date();
}
