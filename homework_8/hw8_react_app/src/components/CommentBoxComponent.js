import React from 'react'
import commentBox from 'commentbox.io'
import PropTypes from 'prop-types'

class CommentBoxComponent extends React.Component{
    componentDidMount(){
        this.removeCommentBox = commentBox('5723716092690432-proj', {
            defaultBoxId: this.props.newsId,
        });
    }
    componentWillUnmount(){
        this.removeCommentBox();
    }
    render(){
        return(
            <div className="commentbox" id={this.props.newsId}></div>
        )
    }
}
CommentBoxComponent.propTypes = {
    newsId : PropTypes.string.isRequired,
}

export default CommentBoxComponent