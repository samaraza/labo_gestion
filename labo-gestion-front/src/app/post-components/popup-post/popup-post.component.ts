import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PostService } from 'src/app/service/post_service';

@Component({
  selector: 'app-popup-post',
  templateUrl: './popup-post.component.html',
  styleUrls: ['./popup-post.component.scss']
})
export class PopupPostComponent implements OnInit {
  postForm: FormGroup;
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupPostComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private postService: PostService
  ) {
    this.postForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      date: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data && this.data.post && this.data.post.id) {
      this.isEditing = true;
      this.postForm.patchValue({
        title: this.data.post.title,
        content: this.data.post.content,
        date: this.data.post.date
      });
    }
  }

  formatDateForBackend(dateStr: string): string {
    if (!dateStr) return '';
    // إذا كانت الصيغة تحتوي على '/' (مثل 20/05/2026)
    if (dateStr.includes('/')) {
      const parts = dateStr.split('/');
      if (parts.length === 3) {
        const day = parts[0].padStart(2, '0');
        const month = parts[1].padStart(2, '0');
        const year = parts[2];
        return `${year}-${month}-${day}T00:00:00`;
      }
    }
    // إذا كانت الصيغة YYYY-MM-DD (من input type="date")، أضف الوقت
    if (dateStr.includes('-') && !dateStr.includes('T')) {
      return dateStr + 'T00:00:00';
    }
    return dateStr;
  }


  submit() {
    if (this.postForm.valid) {
      const formValue = this.postForm.value;
      let dateValue = formValue.date;
      // تنسيق التاريخ للخادم
      if (dateValue) {
        dateValue = this.formatDateForBackend(dateValue);
      }
      const payload: any = {
        title: formValue.title,
        content: formValue.content,
        date: dateValue
      };
      if (this.isEditing && this.data.post.id) {
        payload.id = this.data.post.id;
      }
      this.dialogRef.close(payload);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
