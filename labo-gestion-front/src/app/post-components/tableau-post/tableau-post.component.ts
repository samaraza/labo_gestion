import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Post } from 'src/app/models/post';
import { PostService } from 'src/app/service/post_service';
import { LoginService } from 'src/app/service/login';
import { PopupPostComponent } from '../popup-post/popup-post.component';
import { Role } from 'src/app/enums/role';

@Component({
  selector: 'app-tableau-post',
  templateUrl: './tableau-post.component.html',
  styleUrls: ['./tableau-post.component.scss']
})
export class TableauPostComponent implements OnInit, AfterViewInit {
  posts: Post[] = [];
  headers: any;
  connectedUserRole?: Role;
  roles = Object.keys(Role);

  dataSource: MatTableDataSource<Post> = new MatTableDataSource<Post>(this.posts);
  displayedColumns: string[] = ['title', 'content', 'date', 'action'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private postService: PostService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) {}

  isLoading = false;

  ngOnInit(): void {
    this.connectedUserRole = this.loginService.connectedUser.role;
    this.headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
    });
    this.getAllPosts();
  }

  getAllPosts(): void {
    this.isLoading = true;
    this.postService.getAllPosts().subscribe(
      (data: any) => {
        this.posts = data;
        this.dataSource.data = this.posts;
        this.isLoading = false;
      },
      (error: any) => {
        console.error('Error fetching posts:', error);
        this.isLoading = false;
      }
    );
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  Filterchange(event: Event): void {
    const value = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = value;
  }

  editPost(post: Post): void {
    this.openPopup('Modifier post', post);
  }

  deletePost(id: number): void {
    if (window.confirm('Voulez-vous vraiment supprimer ce post ?')) {
      this.postService.deletePost(id).subscribe(() => {
        this.getAllPosts();
      });
    }
  }

  addPost(): void {
    this.openPopup('Ajouter un post');
  }

  openPopup(title: string, post?: Post): void {
    if (post) {
      const dialogRef = this.dialog.open(PopupPostComponent, {
        width: '40%',
        data: { title, post }
      });
      dialogRef.afterClosed().subscribe((result: any) => {
        if (result) {
          const idPost = post.id!;
          this.updatePostToServer(idPost, result);
        }
      });
    } else {
      const dialogRef = this.dialog.open(PopupPostComponent, {
        width: '40%',
        data: { title, post: new Post() }
      });
      dialogRef.afterClosed().subscribe((result: any) => {
        if (result) {
          this.addPostToServer(result);
        }
      });
    }
  }

  addPostToServer(post: any): void {
    this.postService.addPost(post).subscribe(() => {
      this.getAllPosts();
    });
  }

  updatePostToServer(id: any, post: any): void {
    if (id) {
      this.postService.updatePost(post, id).subscribe(() => {
        this.getAllPosts();
      });
    }
  }
}
