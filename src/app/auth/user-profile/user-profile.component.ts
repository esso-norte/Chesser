import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-user-profile',
    templateUrl: './user-profile.component.html',
    styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
    name: string;
    postLength: number;
    commentLength: number;

    constructor(private activatedRoute: ActivatedRoute) {
        this.name = this.activatedRoute.snapshot.params.name;

        // this.postService.getAllPostsByUser(this.name).subscribe(data => {
        //     this.posts = data;
        //     this.postLength = data.length;
        // });
        // this.commentService.getAllCommentsByUser(this.name).subscribe(data => {
        //     this.comments = data;
        //     this.commentLength = data.length;
        // });
    }

    ngOnInit(): void {
    }

}
