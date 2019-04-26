package com.igalata.bubblepicker.physics

import com.igalata.bubblepicker.model.PickerItem
import com.igalata.bubblepicker.rendering.Item
import com.igalata.bubblepicker.sqr
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import java.util.*

/**
 * Created by irinagalata on 1/26/17.
 */
object Engine {

    val selectedBodies: List<CircleBody>
        get() = bodies.filter { it.increased || it.toBeIncreased || it.isIncreasing }
    var maxSelectedCount: Int? = null
    var radius = 50
        set(value) {
            field = value
            bubbleRadius = interpolate(0.1f, 0.25f, value / 100f)
            standardIncreasedGravity = interpolate(500f, 800f, value / 100f)
        }
    var centerImmediately = false
    var scaleFactor = 1.0f
    var bubbleRadius = 0.17f

    private var standardIncreasedGravity = interpolate(500f, 800f, 0.5f)
    private val world = World(Vec2(0f, 0f), false)
    private val step = 0.0005f
    private val bodies: ArrayList<CircleBody> = ArrayList()
    private var borders: ArrayList<Border> = ArrayList()
    private val resizeStep = 0.005f
    private var scaleX = 0f
    private var scaleY = 0f
    private var touch = false
    private var gravity = 40f
    private var increasedGravity = 55f
    private var gravityCenter = Vec2(0f, 0f)
    private val currentGravity: Float
        get() = if (touch) increasedGravity else gravity
    private val toBeResized = ArrayList<Item>()
    private val startX
        get() = if (centerImmediately) 0.3f else 2.0f
    private var stepsCount = 0
    val r = Random()
    var randomRadius = 0f
    var swipeForceX: Int = 50
    var swipeForceY: Int = 15

    fun build(bodiesCount: ArrayList<PickerItem>, scaleX: Float, scaleY: Float): List<CircleBody> {
        val density = interpolate(0.8f, 0.2f, radius / 100f)
        bodiesCount.forEach {
            val x = if (Random().nextBoolean()) -startX/1.8f else startX/1.8f
            val y = if (Random().nextBoolean()) -0.1f / scaleY else 0.1f / scaleY
            if(it.withRandomRadius) {
                randomRadius = 0.100f + r.nextFloat() * (0.175f - 0.125f)
                bodies.add(CircleBody(world, Vec2(x, y), randomRadius * scaleX, (randomRadius * scaleX) * scaleFactor, density))
            } else {
                bodies.add(CircleBody(world, Vec2(x, y), it.bubbleRadius * scaleX, (it.bubbleRadius * scaleX) * scaleFactor, density))
            }

        }
        this.scaleX = scaleX
        this.scaleY = scaleY
        createBorders()

        return bodies
    }

    fun move() {
        toBeResized.forEach { it.circleBody.resize(resizeStep) }
        world.step(if (centerImmediately) 0.08f else step, 11, 11)
        bodies.forEach { move(it) }
        toBeResized.removeAll(toBeResized.filter { it.circleBody.finished })
        stepsCount++
        if (stepsCount >= 20) {
            centerImmediately = false
        }
    }

    // increasedGravity = standardIncreasedGravity * Math.abs(x * swipeForceX) * Math.abs(y * swipeForceY)
    fun swipe(x: Float, y: Float) {
        if (Math.abs(gravityCenter.x) < 2) gravityCenter.x += -x
        if (Math.abs(gravityCenter.y) < 0.5f / scaleY) gravityCenter.y += y
        increasedGravity = standardIncreasedGravity * Math.abs(x * swipeForceX)
        touch = true
    }

    fun release() {
        gravityCenter.setZero()
        touch = false
        increasedGravity = standardIncreasedGravity
    }

    fun clear() {
        borders.forEach { world.destroyBody(it.itemBody) }
        bodies.forEach { world.destroyBody(it.physicalBody) }
        borders.clear()
        bodies.clear()
    }

    fun resize(item: Item): Boolean {
        if (selectedBodies.size >= maxSelectedCount ?: bodies.size && !item.circleBody.increased) return false

        if (item.circleBody.isBusy) return false

        item.circleBody.defineState()

        toBeResized.add(item)

        return true
    }

    private fun createBorders() {
        borders = arrayListOf(
                Border(world, Vec2(0f, 0.5f / scaleY), Border.HORIZONTAL),
                Border(world, Vec2(0f, -0.5f / scaleY), Border.HORIZONTAL),
                Border(world, Vec2(2.5f, 0f), Border.VERTICAL),
                Border(world, Vec2(-2.5f, 0f), Border.VERTICAL)
        )
    }

    private fun move(body: CircleBody) {
        body.physicalBody.apply {
            body.isVisible = centerImmediately.not()
            val direction = gravityCenter.sub(position)
            val distance = direction.length()
            val gravity = if (body.increased) scaleFactor * currentGravity else currentGravity
            if (distance > step * 200) {
                applyForce(direction.mul(gravity / distance.sqr()), position)
            }
        }
    }

    private fun interpolate(start: Float, end: Float, f: Float) = start + f * (end - start)

}