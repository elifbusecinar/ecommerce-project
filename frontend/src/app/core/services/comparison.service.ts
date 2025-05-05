import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Product } from './product.service';

@Injectable({
  providedIn: 'root'
})
export class ComparisonService {
  private comparisonListSubject = new BehaviorSubject<Product[]>([]);
  comparisonList$ = this.comparisonListSubject.asObservable();

  constructor() {
    const savedList = localStorage.getItem('comparisonList');
    if (savedList) {
      this.comparisonListSubject.next(JSON.parse(savedList));
    }
  }

  addToComparison(product: Product): void {
    const currentList = this.comparisonListSubject.value;
    if (!currentList.find(p => p.id === product.id)) {
      const newList = [...currentList, product];
      this.comparisonListSubject.next(newList);
      localStorage.setItem('comparisonList', JSON.stringify(newList));
    }
  }

  removeFromComparison(productId: number): void {
    const currentList = this.comparisonListSubject.value;
    const newList = currentList.filter(p => p.id !== productId);
    this.comparisonListSubject.next(newList);
    localStorage.setItem('comparisonList', JSON.stringify(newList));
  }

  clearComparison(): void {
    this.comparisonListSubject.next([]);
    localStorage.removeItem('comparisonList');
  }

  getComparisonList(): Product[] {
    return this.comparisonListSubject.value;
  }
} 