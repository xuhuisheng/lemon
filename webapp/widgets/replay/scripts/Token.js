
REPLAY_TOKEN_WIDTH = 10;

/**
 * @description the current token.
 * @author <a href="mailto:lingosurf168@gmail.com">Lingo Surf168</a>
 *
 * @class
 * @param {Node} node node
 * @param {Replay} replay replay
 */
Token = function(node, replay) {
    this.replay = replay;
    this.src = node;

    this.status = 'prepare';
    this.future = 0;
    this.forkIndex = 0;
    this.step = 10;
};

Token.prototype = {
    /**
     * @description initialize.
     */
    init: function() {
        this.x = this.src.x + this.src.w / 2 - REPLAY_TOKEN_WIDTH;
        this.y = this.src.y + this.src.h / 2 - REPLAY_TOKEN_WIDTH;

        if (this.status === 'prepare') {
            this.status = 'waiting';
            this.createImage();
        }
    },

    /**
     * @description create image.
     */
    createImage: function() {
        var dom = document.createElement('img');
        document.getElementById('processGraphMask').appendChild(dom);
        dom.style.position = 'absolute';
        dom.src = REPLAY_TOKEN_IMAGE;
        dom.style.left = this.x + 'px';
        dom.style.top = this.y + 'px';
        this.dom = dom;
    },

    /**
     * @description find next nodes.
     * @return {Array} children
     */
    findNext: function() {
        return this.src.children;
    },

    /**
     * @description find previous nodes.
     * @return {Array} parent
     */
    findPrev: function() {
        return this.src.parent;
    },

    /**
     * @description start move.
     * @param {Number} future step
     * @return {Boolean} need to notify jobExecutor
     */
    startMove: function(future) {
        if (future === 0) {
            return false;
        }
        if (this.status === 'waiting' || this.status === 'prepare') {
            var nodes = future > 0 ? this.findNext() : this.findPrev();
            if (nodes.length === 0) {
                this.future = 0;
                return false;
            }
            for (var i = 0; i < nodes.length; i++) {
                var dest = nodes[i];
                var token = this;
                if (i !== 0) {
                    token = new Token(this.src, replay);
                    this.replay.tokens.push(token);
                }
                token.forkIndex = this.forkIndex + i;
                token.prepare(dest, future);
            }
            return true;
        } else {
            this.future += future;
            return false;
        }
    },

    /**
     * @description prepare token.
     * @param {Node} dest dest node
     * @parma {Number} future step
     */
    prepare: function(dest, future) {
        this.init();

        this.dest = dest;
        this.future = future;
        this.status = 'running';
        this.step = 0;

        this.calculatePoints();
    },

    /**
     * @description calculate points for polyline.
     */
    calculatePoints: function() {
        var x1 = this.src.x + this.src.w / 2 - REPLAY_TOKEN_WIDTH;
        var y1 = this.src.y + this.src.h / 2 - REPLAY_TOKEN_WIDTH;
        var x2 = this.dest.x + this.dest.w / 2 - REPLAY_TOKEN_WIDTH;
        var y2 = this.dest.y + this.dest.h / 2 - REPLAY_TOKEN_WIDTH;

        this.points = [
            [x1, y1]
        ];

        var innerPoints = this.findTransition();
        if (innerPoints.length == 0) {
            var dx = (x2 - x1) / 10;
            var dy = (y2 - y1) / 10;

            for (var i = 0; i < 10; i++) {
                this.points.push([
                    x1 + dx * (i + 1),
                    y1 + dy * (i + 1)
                ]);
            }
        } else if (innerPoints.length == 1) {
            var x3 = innerPoints[0][0] - 10;
            var y3 = innerPoints[0][1] - 10;

            var dx = (x3 - x1) / 5;
            var dy = (y3 - y1) / 5;

            for (var i = 0; i < 5; i++) {
                this.points.push([
                    x1 + dx * (i + 1),
                    y1 + dy * (i + 1)
                ]);
            }

            dx = (x2 - x3) / 5;
            dy = (y2 - y3) / 5;

            for (var i = 0; i < 5; i++) {
                this.points.push([
                    x3 + dx * (i + 1),
                    y3 + dy * (i + 1)
                ]);
            }
        }
    },

    /**
     * @description find transition.
     * @return {Array} inner points
     */
    findTransition: function() {
        var innerPoints = null;

        if (this.future > 0) {
            innerPoints = this.findTransitionByParent();
        } else if (this.future < 0) {
            innerPoints = this.findTransitionByChild();
        }

        if (!innerPoints) {
            innerPoints = [];
        }
        return innerPoints;
    },

    /**
     * @description find transition by parent.
     * @return {Array} inner points
     */
    findTransitionByParent: function() {
        for (var i = 0; i < this.dest.parent.length; i++) {
            var parentNode = this.dest.parent[i];
            if (this.src == parentNode) {
                for (var j = 0; j < parentNode.activity.ts.length; j++) {
                    var t = parentNode.activity.ts[j];
                    if (t.to == this.dest.activity.name) {
                        return t.g;
                    }
                }
            }
        }
        return null;
    },

    /**
     * @description find transition by children.
     * @return {Array} inner points
     */
    findTransitionByChild: function() {
        for (var i = 0; i < this.dest.children.length; i++) {
            var childNode = this.dest.children[i];
            if (this.src == childNode) {
                for (var j = 0; j < this.dest.activity.ts.length; j++) {
                    var t = this.dest.activity.ts[j];
                    if (t.to == childNode.activity.name) {
                        if (!t.g) {
                            return null;
                        }
                        var g = [];
                        for (var i = t.g.length - 1; i >= 0; i--) {
                            g.push(t.g[i]);
                        }
                        return g;
                    }
                }
            }
        }
        return null;
    },

    /**
     * @description token move.
     */
    move: function() {
        this.step++;
        if (this.step > 10) {
            if (this.future !== 0) {
                if (this.future > 0) {
                    this.future--;
                } else {
                    this.future++;
                }
            }

            var node = this.dest;
            if (this.forkIndex > 0) {
                if (node.type == 'fork' || node.type == 'join') {
                    this.destroy();
                    return;
                }
            }
            this.src = node;
            this.init();
            this.status = 'waiting';

            this.startMove(this.future);
        } else {
            this.dom.style.left = this.points[this.step][0] + 'px';
            this.dom.style.top = this.points[this.step][1] + 'px';
        }
    },

    /**
     * @description destory token.
     */
    destroy: function() {
        if (typeof this.dom !== 'undefined') {
            document.getElementById('processGraphMask').removeChild(this.dom);
            delete this.dom;
        }
        this.status = 'removed';
    }
};
